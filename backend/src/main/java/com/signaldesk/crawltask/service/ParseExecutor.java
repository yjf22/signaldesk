package com.signaldesk.crawltask.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Parses fetched content into structured Document data.
 * For HTML: extracts title, text content, metadata.
 * For RSS/XML: extracts feed entries.
 * For PDF: extracts text (simplified - uses filename/URL as title).
 * For NOTE: returns as-is.
 */
@Component
public class ParseExecutor {

    private static final Logger log = LoggerFactory.getLogger(ParseExecutor.class);

    /**
     * Parse HTML content into a list of ParsedItem.
     * For RSS, parse each <item>/<entry>.
     * For regular HTML pages, return a single ParsedItem.
     */
    public List<ParsedItem> parse(String rawContent, String contentType, String sourceUrl) {
        if (rawContent == null || rawContent.isBlank()) {
            return List.of();
        }

        if (contentType != null && (contentType.contains("xml") || contentType.contains("rss")
                || contentType.contains("atom") || rawContent.trim().startsWith("<?xml")
                || rawContent.trim().startsWith("<rss") || rawContent.trim().startsWith("<feed"))) {
            return parseRss(rawContent);
        }

        if (contentType != null && contentType.contains("html")) {
            return parseHtml(rawContent, sourceUrl);
        }

        // Plain text or unknown - treat as single document
        return List.of(createParsedItem("Content", rawContent, sourceUrl, null));
    }

    private List<ParsedItem> parseHtml(String html, String sourceUrl) {
        try {
            Document doc = Jsoup.parse(html, sourceUrl);

            // Remove script, style, nav, footer elements
            doc.select("script, style, nav, footer, header, aside, .sidebar, .nav, .menu, .advertisement").remove();

            List<ParsedItem> hupuItems = parseHupu(doc, sourceUrl);
            if (!hupuItems.isEmpty()) {
                return hupuItems;
            }

            List<ParsedItem> specializedItems = parseHackerNews(doc);
            if (!specializedItems.isEmpty()) {
                return specializedItems;
            }

            List<ParsedItem> articleItems = parseArticleList(doc);
            if (!articleItems.isEmpty()) {
                return articleItems;
            }

            String title = doc.title();
            if (title == null || title.isBlank()) {
                title = "Untitled";
            }

            // Extract main content: prefer <article>, <main>, or <body>
            Elements mainElements = doc.select("article, main");
            String text;
            if (!mainElements.isEmpty()) {
                text = mainElements.text();
            } else {
                text = doc.body() != null ? doc.body().text() : doc.text();
            }

            // Try to extract author from meta tags
            String author = null;
            var authorMeta = doc.select("meta[name=author]").first();
            if (authorMeta != null) {
                author = authorMeta.attr("content");
            }

            // Try to extract published date
            String publishedDate = null;
            var dateMeta = doc.select("meta[property=article:published_time]").first();
            if (dateMeta != null) {
                publishedDate = dateMeta.attr("content");
            }

            String pageUrl = extractBestUrl(doc, sourceUrl);
            return List.of(new ParsedItem(title, text, pageUrl, author, publishedDate));

        } catch (Exception e) {
            log.error("HTML parsing failed", e);
            return List.of(createParsedItem("Parse Error", html.substring(0, Math.min(500, html.length())),
                    sourceUrl, null));
        }
    }

    private List<ParsedItem> parseHupu(Document doc, String sourceUrl) {
        String host = "";
        try {
            host = URI.create(sourceUrl).getHost();
        } catch (Exception ignored) {
        }
        if (host == null || !host.contains("hupu.com")) {
            return List.of();
        }

        LinkedHashSet<String> seenUrls = new LinkedHashSet<>();
        List<ParsedItem> items = new ArrayList<>();

        for (Element linkEl : doc.select("a[href*='bbs.hupu.com']")) {
            String link = linkEl.absUrl("href");
            if (link.isBlank() || !seenUrls.add(link)) {
                continue;
            }

            String title = normalizeWhitespace(linkEl.text());
            if (title.isBlank() || title.length() < 8) {
                continue;
            }
            if (!title.startsWith("[") && !title.startsWith("【") && title.length() < 14) {
                continue;
            }

            String excerpt = extractHupuExcerpt(linkEl);
            String content = excerpt.isBlank() ? title : title + "\n" + excerpt;
            items.add(createParsedItem(title, content, link, null));

            if (items.size() >= 20) {
                break;
            }
        }

        return items;
    }

    private List<ParsedItem> parseHackerNews(Document doc) {
        List<ParsedItem> items = new ArrayList<>();
        Elements rows = doc.select("tr.athing");
        if (rows.isEmpty()) {
            return items;
        }

        for (Element row : rows) {
            Element titleLink = row.selectFirst(".titleline > a, .title a");
            if (titleLink == null) {
                continue;
            }

            String title = titleLink.text();
            String link = titleLink.absUrl("href");
            if (link.isBlank()) {
                link = titleLink.attr("href");
            }

            Element subtextRow = row.nextElementSibling();
            String metaText = subtextRow != null ? subtextRow.text() : "";
            String content = metaText.isBlank() ? title : title + "\n" + metaText;

            items.add(new ParsedItem(
                    title.isBlank() ? "Untitled" : title,
                    content,
                    link.isBlank() ? doc.location() : link,
                    null,
                    null
            ));
        }

        return items;
    }

    private List<ParsedItem> parseArticleList(Document doc) {
        List<ParsedItem> items = new ArrayList<>();
        Elements articles = doc.select("article");
        if (articles.size() < 2) {
            return items;
        }

        for (Element article : articles) {
            Element linkEl = article.selectFirst("h1 a[href], h2 a[href], h3 a[href], a[href]");
            if (linkEl == null) {
                continue;
            }

            String title = linkEl.text();
            if (title.isBlank()) {
                continue;
            }

            String link = linkEl.absUrl("href");
            if (link.isBlank()) {
                link = linkEl.attr("href");
            }

            String text = article.text();
            if (text.isBlank()) {
                text = title;
            }

            items.add(createParsedItem(title, text, link.isBlank() ? doc.location() : link, null));
            if (items.size() >= 20) {
                break;
            }
        }

        return items;
    }

    private String extractBestUrl(Document doc, String fallbackUrl) {
        Element canonical = doc.selectFirst("link[rel=canonical]");
        if (canonical != null) {
            String href = canonical.absUrl("href");
            if (!href.isBlank()) {
                return href;
            }
        }

        Element ogUrl = doc.selectFirst("meta[property=og:url]");
        if (ogUrl != null) {
            String content = ogUrl.attr("content");
            if (content != null && !content.isBlank()) {
                return content;
            }
        }

        return fallbackUrl;
    }

    private String extractHupuExcerpt(Element linkEl) {
        Element parent = linkEl.parent();
        if (parent != null) {
            String parentText = normalizeWhitespace(parent.text());
            if (!parentText.isBlank() && !parentText.equals(normalizeWhitespace(linkEl.text()))) {
                return trimExcerpt(parentText.replace(linkEl.text(), ""));
            }

            Element next = parent.nextElementSibling();
            if (next != null) {
                String nextText = trimExcerpt(next.text());
                if (!nextText.isBlank()) {
                    return nextText;
                }
            }
        }

        StringBuilder siblingText = new StringBuilder();
        for (var node : linkEl.parentNode().childNodes()) {
            if (node instanceof TextNode textNode) {
                siblingText.append(' ').append(textNode.text());
            }
        }
        return trimExcerpt(siblingText.toString());
    }

    private String trimExcerpt(String text) {
        String normalized = normalizeWhitespace(text);
        if (normalized.length() > 300) {
            return normalized.substring(0, 300);
        }
        return normalized;
    }

    private String normalizeWhitespace(String text) {
        if (text == null) {
            return "";
        }
        return text.replace('\u00A0', ' ').replaceAll("\\s+", " ").trim();
    }

    private List<ParsedItem> parseRss(String xml) {
        List<ParsedItem> items = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(xml, "", org.jsoup.parser.Parser.xmlParser());

            // Try RSS <item> elements
            Elements rssItems = doc.select("item");
            if (rssItems.isEmpty()) {
                // Try Atom <entry> elements
                rssItems = doc.select("entry");
            }

            for (var item : rssItems) {
                String title = item.select("title").text();
                String link = item.select("link").text();
                if (link.isBlank()) {
                    // Atom: link may be in href attribute
                    var linkEl = item.select("link").first();
                    if (linkEl != null) {
                        link = linkEl.attr("href");
                    }
                }
                String description = item.select("description, summary, content").text();
                String author = item.select("author name, author").text();
                String pubDate = item.select("pubDate, published, updated").text();

                String content = description.isBlank()
                        ? (title + " " + link)
                        : description;

                items.add(new ParsedItem(
                        title.isBlank() ? "Untitled" : title,
                        content,
                        link.isBlank() ? null : link,
                        author.isBlank() ? null : author,
                        pubDate.isBlank() ? null : pubDate
                ));
            }

            // If no items found, treat the whole feed as one document
            if (items.isEmpty()) {
                String title = doc.select("channel title, feed title").text();
                items.add(createParsedItem(
                        title.isBlank() ? "Feed" : title,
                        doc.text(), null, null));
            }

        } catch (Exception e) {
            log.error("RSS parsing failed", e);
            items.add(createParsedItem("RSS Feed", xml, null, null));
        }

        return items;
    }

    private ParsedItem createParsedItem(String title, String text, String sourceUrl, String author) {
        // Truncate extremely long content for performance
        String truncated = text.length() > 500_000 ? text.substring(0, 500_000) : text;
        return new ParsedItem(title, truncated, sourceUrl, author, null);
    }

    /**
     * Represents a single parsed content item.
     */
    public record ParsedItem(
            String title,
            String content,
            String url,
            String author,
            String publishedDate
    ) {
        public int wordCount() {
            if (content == null) return 0;
            return content.split("\\s+").length;
        }
    }
}
