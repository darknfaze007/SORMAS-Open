package de.symeda.sormas.app.task;

import android.content.Context;
import android.databinding.tool.util.StringUtils;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Space;
import android.widget.TextView;

import java.util.Date;

import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.task.Task;
import de.symeda.sormas.app.util.DataUtils;

/**
 * Created by Stefan Szczesny on 21.07.2016.
 */
public class TasksListArrayAdapter extends ArrayAdapter<Task> {

    private static final String TAG = TasksListArrayAdapter.class.getSimpleName();

    private final Context context;
    private int resource;

    public TasksListArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resource, parent, false);
        }

        Task task = (Task) getItem(position);



        TextView uuid = (TextView) convertView.findViewById(R.id.task_uuid_li);
        AbstractDomainObject associatedLink = task.getAssociatedLink();
        if (associatedLink != null) {
            uuid.setText(DataHelper.getShortUuid(associatedLink.getUuid()));
        }
        setFontStyle(uuid, task.getTaskStatus());

        TextView dueDate = (TextView) convertView.findViewById(R.id.task_dueDate_li);
        dueDate.setText(DateHelper.formatDDMMYYYY(task.getDueDate()));

        Integer dueDateColor = null;
        if(task.getDueDate().compareTo(new Date()) <= 0 && !TaskStatus.DONE.equals(task.getTaskStatus())) {
            dueDateColor = R.color.textColorRed;
        }
        setFontStyle(dueDate, task.getTaskStatus(),dueDateColor);

        TextView taskType = (TextView) convertView.findViewById(R.id.task_taskType_li);
        taskType.setText(DataUtils.toString(task.getTaskType()));
        setFontStyle(taskType, task.getTaskStatus());

        TextView creatorComment = (TextView) convertView.findViewById(R.id.task_creatorComment_li);
        creatorComment.setText(task.getCreatorComment());
        setFontStyle(creatorComment, task.getTaskStatus());

        View priority = (View) convertView.findViewById(R.id.task_priority_li);
        if (task.getPriority() != null) {
            Integer priorityColor = null;
            ;
            switch (task.getPriority()) {
                case HIGH:
                    priorityColor = R.color.textColorRed;
                    break;
                case NORMAL:
                    priorityColor = R.color.colorPrimaryDark;
                    break;
                case LOW:
                    priorityColor = R.color.colorInvisible;
                    break;
            }

            if (priorityColor != null) {
                priority.setBackgroundColor(getContext().getResources().getColor(priorityColor));
            }
        }

        return convertView;
    }


    private void setFontStyle(TextView textView, TaskStatus taskStatus) {
        setFontStyle(textView,taskStatus,null);
    }

    private void setFontStyle(TextView textView, TaskStatus taskStatus, Integer textColorGiven) {
        int fontface = Typeface.NORMAL;
        int textColor = R.color.textColorPrimaryDark;
        textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

//        if(TaskStatus.DONE.equals(taskStatus)) {
//            fontface = Typeface.ITALIC;
//        }
//        else
        if(TaskStatus.DISCARDED.equals(taskStatus)) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textColor = R.color.textColorPrimaryLight;
        }

        if(textColorGiven != null) {
            textColor = textColorGiven;
        }

        textView.setTypeface(null, fontface);
        textView.setTextColor(getContext().getResources().getColor(textColor));
    }
}