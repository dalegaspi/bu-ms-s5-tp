package edu.bu.cs622.jlitebox.options;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dlegaspi@bu.edu
 */
public final class UserOptions implements Serializable {
    private int previewSize = 1;
    private boolean enableGridView = false;
    private boolean showExifOnHover = true;
    private List<String> filters = new ArrayList<>();

    public int getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(int previewSize) {
        this.previewSize = previewSize;
    }

    public boolean isEnableGridView() {
        return enableGridView;
    }

    public void setEnableGridView(boolean enableGridView) {
        this.enableGridView = enableGridView;
    }

    public boolean isShowExifOnHover() {
        return showExifOnHover;
    }

    public void setShowExifOnHover(boolean showExifOnHover) {
        this.showExifOnHover = showExifOnHover;
    }

    public List<String> getFilters() {
        return filters;
    }

    public void setFilters(@NonNull List<String> filters) {
        this.filters = filters;
    }

    @JsonIgnore
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UserOptions)) {
            return false;
        }

        UserOptions otherUserOptions = (UserOptions) other;

        return isEnableGridView() == otherUserOptions.isEnableGridView() &&
                        isShowExifOnHover() == otherUserOptions.isShowExifOnHover() &&
                        getPreviewSize() == otherUserOptions.getPreviewSize() &&
                        getFilters().size() == otherUserOptions.getFilters().size() &&
                        getFilters().containsAll(otherUserOptions.getFilters())
                        && otherUserOptions.getFilters().containsAll(getFilters());
    }
}
