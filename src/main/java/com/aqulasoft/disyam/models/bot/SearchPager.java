package com.aqulasoft.disyam.models.bot;


import lombok.Getter;

@Getter
public abstract class SearchPager {
    private int page = 0;
    private final int total;
    private final int perPage;

    protected SearchPager(int total, int perPage) {
        this.total = total;
        this.perPage = perPage;
    }


    public void nextPage() {
        page++;
        if (total / perPage < page) return;
        updateResults(page);
    }

    public void prevPage() {
        if (page == 0) return;
        page--;
        updateResults(page);
    }

    public boolean hasPages() {
        return total / perPage > 0;
    }

    public abstract void updateResults(int page);
}
