package de.symeda.sormas.app.backend.task;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.util.DataUtils;

@Entity(name= Task.TABLE_NAME)
@DatabaseTable(tableName = Task.TABLE_NAME)
public class Task extends AbstractDomainObject {
	
	private static final long serialVersionUID = -2666695184163562129L;

	public static final String TABLE_NAME = "tasks";
	public static final String I18N_PREFIX = "Task";

	public static final String ASSIGNEE_REPLY = "assigneeReply";
	public static final String ASSIGNEE_USER = "assigneeUser";
	public static final String CAZE = "caze";
	public static final String CONTACT = "contact";
	public static final String CREATOR_COMMENT = "creatorComment";
	public static final String CREATOR_USER = "creatorUser";
	public static final String PRIORITY = "priority";
	public static final String DUE_DATE = "dueDate";
	public static final String SUGGESTED_START = "suggestedStart";
	public static final String EVENT = "event";
	public static final String PERCEIVED_START = "perceivedStart";
	public static final String STATUS_CHANGE_DATE = "statusChangeDate";
	public static final String TASK_CONTEXT = "taskContext";
	public static final String TASK_STATUS = "taskStatus";
	public static final String TASK_TYPE = "taskType";

	@Enumerated(EnumType.STRING)
	private TaskContext taskContext;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 4)
	private Case caze;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 4)
	private Contact contact;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 4)
	private Event event;

	@Enumerated(EnumType.STRING)
	private TaskType taskType;

	@Enumerated(EnumType.STRING)
	private TaskPriority priority;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date dueDate;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date suggestedStart;

	@Enumerated(EnumType.STRING)
	private TaskStatus taskStatus;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date statusChangeDate;

	@DatabaseField(dataType = DataType.DATE_LONG, canBeNull = true)
	private Date perceivedStart;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User creatorUser;

	@Column(length=512)
	private String creatorComment;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User assigneeUser;

	@Column(length=512)
	private String assigneeReply;

	@Column(columnDefinition = "float8")
	private Float closedLat;

	@Column(columnDefinition = "float8")
	private Float closedLon;

	public TaskContext getTaskContext() {
		return taskContext;
	}
	public void setTaskContext(TaskContext taskContext) {
		this.taskContext = taskContext;
	}

	public AbstractDomainObject getAssociatedLink() {
		switch (getTaskContext()) {

			case CASE:
				return getCaze();
			case CONTACT:
				return getContact();
			case EVENT:
				return getEvent();
			case GENERAL:
				return null;
			default:
				throw new IndexOutOfBoundsException(DataHelper.toStringNullable(getTaskContext()));
		}
	}

	public Case getCaze() {
		return caze;
	}
	public void setCaze(Case caze) {
		this.caze = caze;
	}

	public Contact getContact() {
		return contact;
	}
	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}

	public TaskType getTaskType() {
		return taskType;
	}
	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	public Date getPerceivedStart() {
		return perceivedStart;
	}
	public void setPerceivedStart(Date perceivedStart) {
		this.perceivedStart = perceivedStart;
	}

	public User getCreatorUser() {
		return creatorUser;
	}
	public void setCreatorUser(User creatorUser) {
		this.creatorUser = creatorUser;
	}

	public String getCreatorComment() {
		return creatorComment;
	}
	public void setCreatorComment(String creatorComment) {
		this.creatorComment = creatorComment;
	}

	public User getAssigneeUser() {
		return assigneeUser;
	}
	public void setAssigneeUser(User assigneeUser) {
		this.assigneeUser = assigneeUser;
	}

	public String getAssigneeReply() {
		return assigneeReply;
	}
	public void setAssigneeReply(String assigneeReply) {
		this.assigneeReply = assigneeReply;
	}

	public TaskPriority getPriority() {
		return priority;
	}
	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}

	public Date getSuggestedStart() {
		return suggestedStart;
	}
	public void setSuggestedStart(Date suggestedStart) {
		this.suggestedStart = suggestedStart;
	}

	public Float getClosedLat() {
		return closedLat;
	}

	public void setClosedLat(Float closedLat) {
		this.closedLat = closedLat;
	}

	public Float getClosedLon() {
		return closedLon;
	}

	public void setClosedLon(Float closedLon) {
		this.closedLon = closedLon;
	}

	@Override
	public String getI18nPrefix() {
		return I18N_PREFIX;
	}
}
