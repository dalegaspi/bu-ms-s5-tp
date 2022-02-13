package edu.bu.cs622.jlitebox.filter;

import edu.bu.cs622.jlitebox.image.Image;

import java.util.Collection;

/**
 * Subclass of FilteredContentCollection to deal with just Images
 *
 * Ideally this is not created directly; it is usually called from the
 * ImageCatalog::getImages that takes a filter as parameter so outside of
 * testing it should not be visible to the rest of the code outside of
 * ImageCatalog that filtering magic is done using this class.
 *
 * @see edu.bu.cs622.jlitebox.image.ImageCatalog
 * @see edu.bu.cs622.jlitebox.image.BasicImageCatalog
 * @see ImageContentFilter
 * @author dlegaspi@bu.edu
 */
public class FilteredImageContentCollection extends FilteredContentCollection<Image, ImageContentFilter> {

    /**
     * Filtered images collection
     *
     * @param contents collection of images to filter (usually from a Catalog)
     */
    public FilteredImageContentCollection(Collection<Image> contents) {
        super(contents);
    }
}
