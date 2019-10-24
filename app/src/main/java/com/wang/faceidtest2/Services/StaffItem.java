package com.wang.faceidtest2.Services;

import android.widget.Button;
import android.widget.CheckBox;

/**
 * @version $Rev$
 * @auther wangyi
 * @des ${TODO}
 * @updateAuther $Auther$
 * @updateDes ${TODO}
 */
public class StaffItem {
    private Button delete;
    private String id;
    private String name;
    private CheckBox mCheckBox;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CheckBox getCheckBox() {
        return mCheckBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        mCheckBox = checkBox;
    }
}
