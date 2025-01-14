package com.example.pageflow;
public class comment {

        String id, bookId, comment, userId;
        long timestamp;

        public comment() {
        }

        public comment(String id, String bookId, long timestamp, String comment, String userId) {
            this.id = id;
            this.bookId = bookId;
            this.timestamp = timestamp;
            this.comment = comment;
            this.userId = userId;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBookId() {
            return bookId;
        }

        public void setBookId(String bookId) {
            this.bookId = bookId;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
