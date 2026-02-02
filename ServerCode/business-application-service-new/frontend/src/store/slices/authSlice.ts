import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export type UserRole = 'admin' | 'restorer' | 'investor' | 'collector' | null;

interface AuthState {
  isAuthenticated: boolean;
  role: UserRole;
  username: string | null;
}

const initialState: AuthState = {
  isAuthenticated: false,
  role: null,
  username: null,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    login: (state, action: PayloadAction<{ role: UserRole; username: string }>) => {
      state.isAuthenticated = true;
      state.role = action.payload.role;
      state.username = action.payload.username;
    },
    logout: (state) => {
      state.isAuthenticated = false;
      state.role = null;
      state.username = null;
    },
    setRole: (state, action: PayloadAction<UserRole>) => {
      state.role = action.payload;
    },
  },
});

export const { login, logout, setRole } = authSlice.actions;

export const selectIsAdmin = (state: { auth: AuthState }) =>
  state.auth.role === 'admin';

export const selectIsRestorer = (state: { auth: AuthState }) =>
  state.auth.role === 'restorer';

export default authSlice.reducer;
