package edu.ftcc.farmstore.ui;

import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;

public class DefaultRowFilter extends RowFilter<DefaultTableModel, Integer> {
    private final String q;

    public DefaultRowFilter(String q) {
        this.q = (q == null) ? "" : q;
    }

    @Override
    public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> e) {
        if (q.isEmpty()) return true;
        for (int i = 0; i < e.getValueCount(); i++) {
            String v = e.getStringValue(i);
            if (v != null && v.toLowerCase().contains(q)) return true;
        }
        return false;
    }
}