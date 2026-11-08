package com.mzansi.chatzone.util;

/**
 * Minimal HTML escaping for user-supplied text (usernames, display names,
 * message previews/content) before it is printed into a JSP page.
 * Every message a user sends is, by definition, attacker-controlled input
 * from every other user's point of view — this runs on all of it, both
 * server-side (JSP scriptlets) and mirrored client-side (chat.js uses
 * textContent instead of innerHTML for the same reason).
 */
public final class HtmlEscaper {

    private HtmlEscaper() {
    }

    public static String escape(String input) {
        if (input == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&': out.append("&amp;"); break;
                case '<': out.append("&lt;"); break;
                case '>': out.append("&gt;"); break;
                case '"': out.append("&quot;"); break;
                case '\'': out.append("&#x27;"); break;
                default: out.append(c);
            }
        }
        return out.toString();
    }
}
