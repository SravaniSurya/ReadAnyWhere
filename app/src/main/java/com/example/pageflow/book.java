package com.example.pageflow;

public class book {
        private String id;
        private String title;
        private String author;
        private String language;
        private String genere;
        private String image;
        private Boolean isAvailable;
        private String borrowDate;

        public book(){

        }

        public book(String id, String title, String author, String language, String genere, String image, Boolean isAvailable) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.language = language;
            this.genere = genere;
            this.image = image;
            this.isAvailable = isAvailable;
        }

        public book(String id, String title, String author, String language, String genere, String image, Boolean isAvailable, String borrowDate) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.language = language;
            this.genere = genere;
            this.image = image;
            this.isAvailable = isAvailable;
            this.borrowDate = borrowDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getGenere() {
            return genere;
        }

        public void setGenere(String genere) {this.genere = genere;}

        public String getImage() {
            return image;
        }

        public void setImage(String gener) {
            this.image = image;
        }

        public Boolean getIsAvailable() {
            return isAvailable;
        }

        public void setIsAvailable(Boolean isAvailable) {
            this.isAvailable = isAvailable;
        }

        public String getBorrowDate() {return borrowDate;}

        public void setBorrowDate(String borrowDate) {this.borrowDate = borrowDate;}
    }

