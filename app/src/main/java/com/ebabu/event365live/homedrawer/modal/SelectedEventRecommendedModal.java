package com.ebabu.event365live.homedrawer.modal;

public class SelectedEventRecommendedModal {
    private Integer categoryId;
    private Integer subCategoryId;

    public SelectedEventRecommendedModal(Integer categoryId, Integer subCategoryId) {
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public Integer getSubCategoryId() {
        return subCategoryId;
    }
}
