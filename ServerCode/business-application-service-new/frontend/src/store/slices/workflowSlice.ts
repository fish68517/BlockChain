import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import api from '@/services/api';

export interface WorkflowTask {
  id: string;
  name: string;
  processInstanceId: string;
  listingId: number;
  status: 'pending' | 'completed';
  createdAt: string;
}

interface WorkflowState {
  tasks: WorkflowTask[];
  loading: boolean;
  error: string | null;
}

const initialState: WorkflowState = {
  tasks: [],
  loading: false,
  error: null,
};

export const fetchWorkflowTasks = createAsyncThunk(
  'workflow/fetchTasks',
  async () => {
    const response = await api.get('/workflow/tasks');
    return response.data as WorkflowTask[];
  }
);

export const completeTask = createAsyncThunk(
  'workflow/completeTask',
  async (taskId: string) => {
    await api.post(`/workflow/tasks/${taskId}/complete`);
    return taskId;
  }
);

const workflowSlice = createSlice({
  name: 'workflow',
  initialState,
  reducers: {
    clearTasks: (state) => {
      state.tasks = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchWorkflowTasks.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchWorkflowTasks.fulfilled, (state, action) => {
        state.loading = false;
        state.tasks = action.payload;
      })
      .addCase(fetchWorkflowTasks.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch tasks';
      })
      .addCase(completeTask.fulfilled, (state, action) => {
        state.tasks = state.tasks.filter((t) => t.id !== action.payload);
      });
  },
});

export const { clearTasks } = workflowSlice.actions;
export default workflowSlice.reducer;
