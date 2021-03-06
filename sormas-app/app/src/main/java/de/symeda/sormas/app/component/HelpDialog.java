package de.symeda.sormas.app.component;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;

import de.symeda.sormas.app.R;


public class HelpDialog  {

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private String title;

    public HelpDialog(Context context) {
        builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(context.getResources().getText(R.string.action_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        title = context.getResources().getText(R.string.headline_help).toString();
    }

    public void setMessage(String message) {
        builder.setMessage(message);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void show() {
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setTitle(title);
        dialog.show();
    }

    /**
     * Finds all PropertyFields in the given template-form and appends the labels to a text.
     * @param form
     * @return
     */
    public static String getHelpForForm(LinearLayout form) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < form.getChildCount(); i++) {
            if (form.getChildAt(i) instanceof PropertyField) {
                PropertyField propertyField = (PropertyField)form.getChildAt(i);
                sb
                        .append("<b>"+propertyField.getCaption()+"</b>").append("<br>")
                        .append(propertyField.getDescription()).append("<br>").append("<br>");
            }
        }

        return sb.toString();
    }


}
