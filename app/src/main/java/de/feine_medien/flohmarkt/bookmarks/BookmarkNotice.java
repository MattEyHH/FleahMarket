package de.feine_medien.flohmarkt.bookmarks;

public enum BookmarkNotice {

    GOING("0"), NOT_SURE("1"), WANT_VISIT("2"), NO_PLACE("3");

    private final String noticeId;

    BookmarkNotice(final String id) {
        noticeId = id;
    }

    public String getNoticeId() {
        return noticeId;
    }
}
