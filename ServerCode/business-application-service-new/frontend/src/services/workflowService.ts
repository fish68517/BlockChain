import api from './api';
import type { ApiResponse } from '@/types';

export interface WorkflowTask {
  id: number;
  name: string;
  status: string;
  listingId?: number;
  createdAt?: string;
}

export const workflowService = {
  getTasks: () => api.get<WorkflowTask[]>('/workflow/tasks'),

  completeTask: (taskId: number, data?: Record<string, unknown>) =>
    api.post<ApiResponse>(`/workflow/tasks/${taskId}/complete`, data),
};
