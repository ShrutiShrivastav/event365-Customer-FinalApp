package com.ebabu.event365live.home.modal.nearbymodal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLikes implements Parcelable {
    @SerializedName("isLike")
    @Expose
    private Boolean isLike;
    @SerializedName("isDisLike")
    @Expose
    private Boolean isDisLike;

    protected UserLikes(Parcel in) {
        byte tmpIsLike = in.readByte();
        isLike = tmpIsLike == 0 ? null : tmpIsLike == 1;
        byte tmpIsDisLike = in.readByte();
        isDisLike = tmpIsDisLike == 0 ? null : tmpIsDisLike == 1;
    }

    public static final Creator<UserLikes> CREATOR = new Creator<UserLikes>() {
        @Override
        public UserLikes createFromParcel(Parcel in) {
            return new UserLikes(in);
        }

        @Override
        public UserLikes[] newArray(int size) {
            return new UserLikes[size];
        }
    };

    public Boolean getLike() {
        return isLike;
    }

    public void setLike(Boolean like) {
        isLike = like;
    }

    public Boolean getDisLike() {
        return isDisLike;
    }

    public void setDisLike(Boolean disLike) {
        isDisLike = disLike;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isLike == null ? 0 : isLike ? 1 : 2));
        dest.writeByte((byte) (isDisLike == null ? 0 : isDisLike ? 1 : 2));
    }
}
