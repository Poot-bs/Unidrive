async function registerUser(event) {
  event.preventDefault();
  await safeCall(async () => {
    const payload = {
      nom: byId("nom").value,
      email: byId("email").value,
      password: byId("password").value,
      role: byId("role").value,
    };
    const user = await API.request("/auth/register", "POST", payload);
    setCurrentUser({
      userId: user.identifiant,
      role: user.role,
      name: user.nom,
      email: user.email,
    });
    showStatus("status", `Compte cree. Bienvenue ${user.nom}.`);
    setTimeout(() => {
      redirectToRoleHome({
        userId: user.identifiant,
        role: user.role,
        name: user.nom,
        email: user.email,
      });
    }, 600);
  }, "status");
}

async function loginUser(event) {
  event.preventDefault();
  await safeCall(async () => {
    const payload = {
      email: byId("email").value,
      password: byId("password").value,
    };
    const data = await API.request("/auth/login", "POST", payload);
    API.setToken(data.token);
    setCurrentUser(data);
    showStatus("status", "Connexion reussie");
    setTimeout(() => {
      redirectToRoleHome(data);
    }, 500);
  }, "status");
}
