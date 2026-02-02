import { Navigate } from 'react-router-dom';

type UserRole = 'ADMIN' | 'OWNER' | 'RESTORER' | 'INVESTOR' | 'BUYER' | 'USER';

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: UserRole[];
  requireAuth?: boolean;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  allowedRoles,
  requireAuth = true,
}) => {
  const token = localStorage.getItem('token');
  const role = localStorage.getItem('role') as UserRole | null;

  if (requireAuth && !token) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && allowedRoles.length > 0) {
    if (!role || !allowedRoles.includes(role)) {
      return <Navigate to="/" replace />;
    }
  }

  return <>{children}</>;
};
