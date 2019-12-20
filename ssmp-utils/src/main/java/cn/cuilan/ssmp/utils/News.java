package cn.cuilan.ssmp.utils;

import org.jsoup.nodes.Element;

/*******************************************************************************
 * Copyright (c) 2005-2016 LongDai, Inc.
 * <p>
 * Contributors:
 * DonQuixote  on 10/26/16 - 8:02 PM
 *******************************************************************************/
public class News {

    protected String url = null;
    protected String title = null;
    protected String content = null;
    protected String time = null;

    protected Element contentElement = null;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        if (content == null) {
            if (contentElement != null) {
                content = contentElement.text();
            }
        }
        return content;
    }



    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "URL:\n" + url + "\nTITLE:\n" + title + "\nTIME:\n" + time + "\nCONTENT:\n" + getContent() + "\nCONTENT(SOURCE):\n" + contentElement;
    }

    public Element getContentElement() {
        return contentElement;
    }

    public void setContentElement(Element contentElement) {
        this.contentElement = contentElement;
    }
}
