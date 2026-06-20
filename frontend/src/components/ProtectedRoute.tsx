import { Navigate } from "react-router-dom";
import {
  getToken,
  isAdminUser
} from "../utils/auth";

interface Props {
  children: React.ReactNode;
  adminOnly?: boolean;
}

export default function ProtectedRoute({
  children,
  adminOnly = false,
}: Props) {
  const token =
    getToken();

  if (!token) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  if (adminOnly && !isAdminUser()) {
    return (
      <Navigate
        to="/"
        replace
      />
    );
  }

  return children;
}
