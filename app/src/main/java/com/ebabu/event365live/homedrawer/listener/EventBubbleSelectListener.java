package com.ebabu.event365live.homedrawer.listener;

import com.ebabu.event365live.homedrawer.modal.SelectedEventCategoryModal;
import com.kienht.bubblepicker.model.PickerItem;

public interface EventBubbleSelectListener {
    void selectBubble(PickerItem pickerItem);
    void unSelectBubble(PickerItem pickerItem);
}
