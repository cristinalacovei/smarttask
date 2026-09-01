export interface Task {
  id: number;
  title: string;
  description: string;
  priority: "LOW" | "MEDIUM" | "HIGH";
  status: "TODO" | "IN_PROGRESS" | "DONE";
  deadline: string;
  createdAt: string;
  updatedAt: string;
  userId: number;
}
