package com.smarttask.ai_service.dto;


import java.util.List;

public class StudyPlanRequest {

    private List<TaskInput> tasks;

    public StudyPlanRequest() {
    }

    public List<TaskInput> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskInput> tasks) {
        this.tasks = tasks;
    }

    public static class TaskInput {

        private String title;
        private String description;
        private String priority;
        private String deadline;

        public TaskInput() {
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }
    }
}