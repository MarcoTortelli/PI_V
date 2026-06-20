type JwtPayload = Record<string, unknown>;

function decodeTokenPayload(token: string): JwtPayload | null {
  const [, payload] =
    token.split(".");

  if (!payload) {
    return null;
  }

  try {
    const normalized =
      payload
        .replace(/-/g, "+")
        .replace(/_/g, "/");

    const json =
      decodeURIComponent(
        atob(normalized)
          .split("")
          .map((char) =>
            `%${char.charCodeAt(0).toString(16).padStart(2, "0")}`
          )
          .join("")
      );

    return JSON.parse(json);
  } catch {
    return null;
  }
}

function collectRoleValues(value: unknown): string[] {
  if (!value) {
    return [];
  }

  if (typeof value === "string") {
    return value.split(/[,\s]+/);
  }

  if (Array.isArray(value)) {
    return value.flatMap((item) => {
      if (typeof item === "string") {
        return [item];
      }

      if (
        item &&
        typeof item === "object" &&
        "authority" in item
      ) {
        return collectRoleValues(
          (item as { authority?: unknown }).authority
        );
      }

      if (
        item &&
        typeof item === "object"
      ) {
        const roleObject =
          item as Record<string, unknown>;

        return [
          ...collectRoleValues(roleObject.name),
          ...collectRoleValues(roleObject.role),
          ...collectRoleValues(roleObject.permission),
        ];
      }

      return [];
    });
  }

  return [];
}

export function getToken() {
  return localStorage.getItem("token");
}

export function isAdminUser() {
  const token = getToken();

  if (!token) {
    return false;
  }

  const payload =
    decodeTokenPayload(token);

  if (!payload) {
    return false;
  }

  const values = [
    ...collectRoleValues(payload.role),
    ...collectRoleValues(payload.roles),
    ...collectRoleValues(payload.authorities),
    ...collectRoleValues(payload.authority),
    ...collectRoleValues(payload.permissions),
    ...collectRoleValues(payload.permission),
    ...collectRoleValues(payload.scope),
    ...collectRoleValues(payload.scopes),
    ...collectRoleValues(payload.userRole),
    ...collectRoleValues(payload.userType),
    ...collectRoleValues(payload.roleType),
    ...collectRoleValues(payload.perfil),
  ];

  if (
    payload.admin === true ||
    payload.isAdmin === true
  ) {
    return true;
  }

  return values.some((value) => {
    const role =
      value.toUpperCase();

    return role === "ADMIN" || role === "ROLE_ADMIN";
  });
}
