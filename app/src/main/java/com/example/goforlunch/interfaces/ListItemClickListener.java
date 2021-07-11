package com.example.goforlunch.interfaces;

/**
 * interface implemented by the mapActivity to manage the
 * click on a restaurant either on the marker on the map or
 * on the item in the recyclerview.
 */
public interface ListItemClickListener {
    void itemClick(String placeId);

}
