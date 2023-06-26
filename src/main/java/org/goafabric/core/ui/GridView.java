package org.goafabric.core.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public abstract class GridView<T> extends VerticalLayout {
    private final Grid<T> grid;
    private final TextField filterText = new TextField("", "search ...");
    private final SearchLogic<T> logic;

    public GridView(Grid<T> grid, SearchLogic<T> logic) {
        this.grid = grid;
        this.logic = logic;
        createView();
    }

    private void createView() {
        setSizeFull();

        addFilterText();
        addGrid();

        updateList();
    }

    private void addFilterText() {
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        this.add(filterText);
    }

    private void addGrid() {
        grid.setSizeFull();
        grid.setColumns();
        addColumns(grid);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        this.add(grid);
    }


    protected abstract void addColumns(Grid<T> grid);

    protected void updateList() {
        long start = System.currentTimeMillis();
        grid.setItems(logic.search(filterText.getValue()));
        Notification.show("Search took " + (System.currentTimeMillis() -start) + " ms");
    }

    protected SearchLogic<T> getLogic() {
        return logic;
    }

}