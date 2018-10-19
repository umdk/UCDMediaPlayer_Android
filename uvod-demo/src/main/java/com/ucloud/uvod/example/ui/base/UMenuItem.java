package com.ucloud.uvod.example.ui.base;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw.tan on 2015/10/10.
 */
public class UMenuItem implements Parcelable {
    public final String title;
    public final String type;
    public final String description;
    public final UMenuItem parent;
    public int defaultSelected;
    public final boolean isVisible;
    public final int index;

    public List<UMenuItem> childs;

    private UMenuItem(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.parent = builder.parent;
        this.childs = builder.childs;
        this.defaultSelected = builder.defaultSelected;
        this.type = builder.type;
        this.isVisible = builder.isVisible;
        this.index = builder.index;
    }

    public void defaultSelected(int defaultSelected) {
        this.defaultSelected = defaultSelected;
    }

    public static class Builder {
        private String title;
        public String type;
        private String description;
        private UMenuItem parent;
        private int defaultSelected = 0;
        private boolean isVisible;
        public List<UMenuItem> childs = new ArrayList<>();
        private int index;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder decription(String description) {
            this.description = description;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder index(int index) {
            this.index = index;
            return this;
        }

        public Builder defaultSelectedIndex(int defaultIndex) {
            this.defaultSelected = defaultIndex;
            return this;
        }

        public Builder parent(UMenuItem parent) {
            this.parent = parent;
            return this;
        }

        public Builder isVisible(boolean isVisible) {
            this.isVisible = isVisible;
            return this;
        }

        public UMenuItem builder() {
            return new UMenuItem(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.type);
        dest.writeString(this.description);
        dest.writeParcelable(this.parent, flags);
        dest.writeInt(this.defaultSelected);
        dest.writeByte(this.isVisible ? (byte) 1 : (byte) 0);
        dest.writeInt(this.index);
        dest.writeTypedList(this.childs);
    }

    protected UMenuItem(Parcel in) {
        this.title = in.readString();
        this.type = in.readString();
        this.description = in.readString();
        this.parent = in.readParcelable(UMenuItem.class.getClassLoader());
        this.defaultSelected = in.readInt();
        this.isVisible = in.readByte() != 0;
        this.index = in.readInt();
        this.childs = in.createTypedArrayList(UMenuItem.CREATOR);
    }

    public static final Creator<UMenuItem> CREATOR = new Creator<UMenuItem>() {
        @Override
        public UMenuItem createFromParcel(Parcel source) {
            return new UMenuItem(source);
        }

        @Override
        public UMenuItem[] newArray(int size) {
            return new UMenuItem[size];
        }
    };
}
