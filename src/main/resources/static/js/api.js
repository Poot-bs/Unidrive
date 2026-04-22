const API = {
  base: "/api",
  token: localStorage.getItem("token") || "",

  setToken(token) {
    this.token = token;
    localStorage.setItem("token", token);
  },

  headers() {
    const headers = { "Content-Type": "application/json" };
    if (this.token) {
      headers.Authorization = `Bearer ${this.token}`;
    }
    return headers;
  },

  async request(path, method = "GET", body = null) {
    const res = await fetch(`${this.base}${path}`, {
      method,
      headers: this.headers(),
      body: body ? JSON.stringify(body) : null,
    });
    if (!res.ok) {
      let err = "Request failed";
      try {
        const payload = await res.json();
        err = payload.error || err;
      } catch (_) {
        // ignore parsing failure
      }
      throw new Error(err);
    }
    if (res.status === 204) {
      return null;
    }
    return await res.json();
  },
};
