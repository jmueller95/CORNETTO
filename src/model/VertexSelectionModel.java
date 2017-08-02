package model;

import graph.MyVertex;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.MultipleSelectionModel;

import java.util.Arrays;

/**
 * Created by caspar on 15.07.17.
 * adapted from Huson (12/15/15)
 */

public class VertexSelectionModel<T> extends MultipleSelectionModel<T> {
    public final ObservableSet<Integer> selectedIndices; // the set of selected indices

    private T[] items; // need a copy of this array to map indices to objects, when required

    private int focusIndex = -1; // focus index

    private final ObservableList<Integer> unmodifiableSelectedIndices; // unmodifiable list of selected indices
    private final ObservableList<T> unmodifiableSelectedItems; // unmodifiable list of selected items

    /**
     * Constructor
     *
     * @param items 0 or more items
     */
    @SafeVarargs
    public VertexSelectionModel(T... items) {
        this.items = Arrays.copyOf(items, items.length);  // use copy for safety
        selectedIndices = FXCollections.observableSet();

        // setup unmodifiable lists
        {
            // first setup observable array lists that listen for changes of the selectedIndices set
            final ObservableList<Integer> selectedIndicesAsList = FXCollections.observableArrayList();
            final ObservableList<T> selectedItems = FXCollections.observableArrayList();
            selectedIndices.addListener((SetChangeListener<Integer>) c -> {
                if (c.wasAdded()) {
                    selectedIndicesAsList.add(c.getElementAdded());
                    selectedItems.add(getItems()[c.getElementAdded()]);
                } else if (c.wasRemoved()) {
                    selectedIndicesAsList.remove(c.getElementRemoved());
                    selectedItems.remove(getItems()[c.getElementRemoved()]);
                }
            });
            // wrap a unmodifiable observable list around the observable arrays lists
            unmodifiableSelectedIndices = FXCollections.unmodifiableObservableList(selectedIndicesAsList);
            unmodifiableSelectedItems = FXCollections.unmodifiableObservableList(selectedItems);
        }
    }

    @Override
    public ObservableList<Integer> getSelectedIndices() {
        return unmodifiableSelectedIndices;
    }

    @Override
    public ObservableList<T> getSelectedItems() {
        return unmodifiableSelectedItems;
    }

    @Override
    public void selectIndices(int index, int... indices) {
        select(index);
        for (int i : indices) {
            select(i);
        }
    }

    @Override
    public void selectAll() {
        for (int index = 0; index < items.length; index++) {
            selectedIndices.add(index);
        }
        focusIndex=-1;
    }

    @Override
    public void clearAndSelect(int index) {
        clearSelection();
        select(index);
    }

    @Override
    public void
    select(int index) {
        if (index >= 0 && index < items.length) {
            selectedIndices.add(index);
            focusIndex = index;
        }
    }

    @Override
    public void select(T item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)) {
                select(i);
                System.out.println("Added Node to selection Model");
                return;
            }
        }
    }

    @Override
    public void clearSelection(int index) {
        if (index >= 0 && index < items.length) {
            selectedIndices.remove(index);
        }
    }

    @Override
    public void clearSelection() {
        selectedIndices.clear();
        focusIndex = -1;
    }

    @Override
    public boolean isSelected(int index) {
        return index >= 0 && index < items.length && selectedIndices.contains(index);
    }

    @Override
    public boolean isEmpty() {
        return selectedIndices.isEmpty();
    }

    @Override
    public void selectFirst() {
        if (items.length > 0) {
            select(0);
        }
    }

    @Override
    public void selectLast() {
        if (items.length > 0) {
            select(items.length - 1);
        }
    }

    @Override
    public void selectPrevious() {
        select(focusIndex-1);
    }

    @Override
    public void selectNext() {
        select(focusIndex+1);
    }

    /**
     * get the current array of items.
     *
     * @return items
     */
    public T[] getItems() {
        return items;
    }

    /**
     * set the array of items and clear selection
     *
     * @param items
     */
    public void setItems(T[] items) {
        clearSelection();
        this.items = Arrays.copyOf(items, items.length); // use copy for safety
    }

    public void toggleSelect(T item){
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(item)) {
                if (!isSelected(i)){
                    select(i);
                    System.out.println("Toggle node selection: ON");
                }
                else if (isSelected(i)){
                    selectedIndices.remove(i);
                    clearSelection(i);
                    System.out.println("Toggle node selection: OFF");
                }
                return;
            }
        }

    }


}