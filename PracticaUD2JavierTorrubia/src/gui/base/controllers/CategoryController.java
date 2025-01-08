package gui.base.controllers;

import gui.View;
import gui.base.models.MainModel;
import util.Util;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class CategoryController {

    private View view;
    private MainModel mainModel;
    private MainController mainController;

    public CategoryController(View view, MainModel mainModel, MainController mainController) {
        this.view = view;
        this.mainModel = mainModel;
        this.mainController = mainController;
    }

    void addCategory() {
        try {
            if (!checkCategoryFields()) {
                Util.showErrorAlert("Rellena todos los campos");
                return;
            } else if (mainModel.categoryNameExists(view.txtCategoryName.getText())) {
                Util.showErrorAlert("Ya existe una categoría con ese nombre");
                return;
            } else {
                mainModel.insertCategory(
                        view.txtCategoryName.getText(),
                        view.txtCategoryDescription.getText()
                );
            }
        } catch (Exception e) {
            Util.showErrorAlert("Ha ocurido un error al insertar la categoría");
            return;
        }
        Util.showSuccessDialog("Categoria insertada correctamente");
        deleteCategoryFields();
        refreshCategories();
    }

    public boolean checkCategoryFields() {
        return !view.txtCategoryName.getText().isEmpty() && !view.txtCategoryDescription.getText().isEmpty();
    }

    public void deleteCategoryFields() {
        view.txtCategoryName.setText("");
        view.txtCategoryDescription.setText("");
    }

    public void refreshCategories() {
        try {
            view.categoriesTable.setModel(buildTableModelCategories(mainModel.searchCategories()));
            view.comboCategory.removeAllItems();
            for(int i = 0; i < view.dtmCategories.getRowCount(); i++) {
                view.comboCategory.addItem(view.dtmCategories.getValueAt(i, 0)+" - "+
                        view.dtmCategories.getValueAt(i, 1));
            }
            view.comboCategory.setSelectedIndex(-1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DefaultTableModel buildTableModelCategories(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        mainController.setDataVector(rs, columnCount, data);

        view.dtmCategories.setDataVector(data, columnNames);

        return view.dtmCategories;

    }

    void fillCategoryFields() {
        int row = view.categoriesTable.getSelectedRow();
        view.txtCategoryName.setText(String.valueOf(view.categoriesTable.getValueAt(row, 1)));
        view.txtCategoryDescription.setText(String.valueOf(view.categoriesTable.getValueAt(row, 2)));
    }
}
