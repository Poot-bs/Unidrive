function byId(id) {
  return document.getElementById(id);
}

const ROLE_HOME = {
  PASSAGER: "/pages/search-trips.html",
  CHAUFFEUR: "/pages/create-trip.html",
  ADMIN: "/pages/admin.html",
};

const ROLE_ALLOWED_PATHS = {
  PASSAGER: [
    "/",
    "/index.html",
    "/pages/search-trips.html",
    "/pages/my-reservations.html",
    "/pages/notifications.html",
    "/pages/login.html",
    "/pages/register.html",
  ],
  CHAUFFEUR: [
    "/",
    "/index.html",
    "/pages/create-trip.html",
    "/pages/driver-reservations.html",
    "/pages/notifications.html",
    "/pages/login.html",
    "/pages/register.html",
  ],
  ADMIN: [
    "/",
    "/index.html",
    "/pages/admin.html",
    "/pages/notifications.html",
    "/pages/login.html",
    "/pages/register.html",
  ],
};

const ROLE_QUICK_LINKS = {
  PASSAGER: [
    { href: "/pages/search-trips.html", label: "Trajets" },
    { href: "/pages/my-reservations.html", label: "Mes reservations" },
    { href: "/pages/notifications.html", label: "Notifications" },
  ],
  CHAUFFEUR: [
    { href: "/pages/create-trip.html", label: "Mes trajets" },
    { href: "/pages/driver-reservations.html", label: "Demandes" },
    { href: "/pages/notifications.html", label: "Notifications" },
  ],
  ADMIN: [
    { href: "/pages/admin.html", label: "Dashboard" },
    { href: "/pages/notifications.html", label: "Notifications" },
  ],
};

function showStatus(id, message, isError = false) {
  const el = byId(id);
  if (!el) return;
  el.textContent = message;
  el.style.color = isError ? "#b00020" : "#125b26";
}

function setUserId(userId) {
  localStorage.setItem("userId", userId);
}

function getUserId() {
  return localStorage.getItem("userId") || "";
}

function normalizePath(pathname) {
  if (!pathname) return "/";
  return pathname.endsWith("/") && pathname.length > 1
    ? pathname.slice(0, -1)
    : pathname;
}

function getRoleHome(role) {
  return ROLE_HOME[role] || "/";
}

function isPathAllowedForRole(pathname, role) {
  const allowed = ROLE_ALLOWED_PATHS[role];
  if (!allowed) return false;
  const currentPath = normalizePath(pathname);
  return allowed.includes(currentPath);
}

function setCurrentUser(user) {
  localStorage.setItem("currentUser", JSON.stringify(user || {}));
  if (user && user.userId) {
    setUserId(user.userId);
  }
}

function getCurrentUser() {
  try {
    return JSON.parse(localStorage.getItem("currentUser") || "{}");
  } catch (_) {
    return {};
  }
}

function hasActiveSession() {
  const user = getCurrentUser();
  return Boolean(user && user.userId && user.role);
}

function redirectToRoleHome(user = getCurrentUser()) {
  if (!user || !user.role) {
    window.location.href = "/pages/login.html";
    return;
  }
  window.location.href = getRoleHome(user.role);
}

function requireCurrentUser(statusId, expectedRole = "") {
  const user = getCurrentUser();
  if (!user.userId) {
    showStatus(statusId, "Connectez-vous d'abord.", true);
    if (!window.location.pathname.endsWith("/pages/login.html")) {
      setTimeout(() => {
        window.location.href = "/pages/login.html";
      }, 600);
    }
    return null;
  }
  if (expectedRole && user.role !== expectedRole) {
    showStatus(statusId, `Cette page est reservee au role ${expectedRole}.`, true);
    setTimeout(() => {
      redirectToRoleHome(user);
    }, 900);
    return null;
  }
  return user;
}

async function safeCall(fn, statusId) {
  try {
    await fn();
  } catch (e) {
    showStatus(statusId, e.message, true);
  }
}

function applyRoleNavigationVisibility(user) {
  const links = document.querySelectorAll(".site-nav a[href]");
  links.forEach((link) => {
    const href = link.getAttribute("href") || "";
    if (!href.startsWith("/pages/")) {
      return;
    }
    if (!user || !user.role) {
      if (href === "/pages/admin.html") {
        link.classList.add("hidden");
      }
      return;
    }
    if (!isPathAllowedForRole(href, user.role)) {
      link.classList.add("hidden");
    }
  });
}

function upsertRoleQuickLinks(user) {
  if (!user || !user.role) return;
  const nav = document.querySelector(".site-nav");
  if (!nav) return;

  const existingQuick = nav.querySelector(".role-quick-links");
  if (existingQuick) {
    existingQuick.remove();
  }

  const links = ROLE_QUICK_LINKS[user.role] || [];
  if (links.length === 0) return;

  const container = document.createElement("div");
  container.className = "role-quick-links";

  links.forEach((item) => {
    const link = document.createElement("a");
    link.className = "nav-link";
    link.href = item.href;
    link.textContent = item.label;
    if (normalizePath(window.location.pathname) === normalizePath(item.href)) {
      link.classList.add("nav-link-active");
    }
    container.appendChild(link);
  });

  const authActions = byId("auth-actions");
  if (authActions && authActions.parentElement === nav) {
    nav.insertBefore(container, authActions);
  } else {
    nav.appendChild(container);
  }
}

async function refreshRoleNotificationIndicators(user) {
  if (!user || !user.userId || !user.role || typeof API === "undefined") return;
  const nav = document.querySelector(".site-nav");
  if (!nav) return;

  const demandLink = Array.from(nav.querySelectorAll("a")).find((a) =>
    (a.getAttribute("href") || "").includes("/pages/driver-reservations.html")
  );
  const notifLink = Array.from(nav.querySelectorAll("a")).find((a) =>
    (a.getAttribute("href") || "").includes("/pages/notifications.html")
  );

  try {
    if (user.role === "CHAUFFEUR" && demandLink) {
      const demandes = await API.request(`/reservations/chauffeur/${user.userId}/demandes`);
      const pending = demandes.filter((d) => d.reservationStatus === "PENDING").length;
      demandLink.setAttribute("data-badge", pending > 0 ? String(pending) : "");
    }

    if (notifLink) {
      const notifications = await API.request(`/users/${user.userId}/notifications`);
      const read = new Set(JSON.parse(localStorage.getItem("readNotifications") || "[]"));
      const unread = notifications.filter((n) => !read.has(n.id)).length;
      notifLink.setAttribute("data-badge", unread > 0 ? String(unread) : "");
    }
  } catch (_) {
    // Fail silently to avoid blocking page rendering on indicator issues.
  }
}

function enforcePageAccessByRole() {
  const path = normalizePath(window.location.pathname);
  if (path === "/pages/login.html" || path === "/pages/register.html") {
    if (hasActiveSession()) {
      redirectToRoleHome();
    }
    return;
  }

  if (!hasActiveSession()) {
    return;
  }

  const user = getCurrentUser();
  if (!isPathAllowedForRole(path, user.role)) {
    redirectToRoleHome(user);
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const user = getCurrentUser();
  const authActions = byId("auth-actions");
  const userProfile = byId("user-profile");
  const userGreeting = byId("user-greeting");

  if (user && user.userId && authActions && userProfile && userGreeting) {
    authActions.classList.add("hidden");
    userProfile.classList.remove("hidden");
    userGreeting.textContent = `Bonjour, ${user.name || "Utilisateur"}`;
  }

  applyRoleNavigationVisibility(user);
  upsertRoleQuickLinks(user);
  enforcePageAccessByRole();
  refreshRoleNotificationIndicators(user);
});

async function logout() {
  try {
    if (typeof API !== "undefined" && API.token) {
      await API.request("/auth/logout", "POST");
    }
  } catch (_) {
    // Ignore API logout failure and clear client session anyway.
  }

  localStorage.removeItem("token");
  localStorage.removeItem("currentUser");
  localStorage.removeItem("userId");
  window.location.href = "/";
}
