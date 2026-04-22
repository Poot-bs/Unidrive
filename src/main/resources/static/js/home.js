function renderFeaturedTrips(trips) {
  const container = byId("featuredTrips");
  if (!container) return;
  container.innerHTML = "";

  if (!Array.isArray(trips) || trips.length === 0) {
    container.innerHTML = "<p class='text-muted small'>Aucun trajet ouvert pour le moment.</p>";
    return;
  }

  trips.slice(0, 3).forEach((trip) => {
    const card = document.createElement("article");
    card.className = "card";
    card.innerHTML = `
      <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:12px;">
        <div>
          <h4 style="margin:0;">${trip.depart} -> ${trip.arrivee}</h4>
          <p class="small text-muted" style="margin:6px 0 0 0;">Depart: ${new Date(trip.dateDepart).toLocaleString()}</p>
        </div>
        <span class="badge badge-primary">${trip.etat}</span>
      </div>
      <p class="small text-muted" style="margin:8px 0 0 0;">Prix: <strong>${trip.prixParPlace} EUR</strong> | Places: <strong>${trip.placesDisponibles}</strong></p>
      <div class="form-actions" style="justify-content:flex-start; margin-top:10px;">
        <a class="btn btn-secondary btn-sm" href="/pages/search-trips.html">Reserver</a>
      </div>
    `;
    container.appendChild(card);
  });
}

async function refreshLandingData() {
  const status = byId("liveStatus");
  try {
    const [dashboard, health, trips] = await Promise.all([
      API.request("/admin/dashboard"),
      API.request("/system/health"),
      API.request("/trajets"),
    ]);

    byId("liveUsers").textContent = String(dashboard.users ?? "-");
    byId("liveTrips").textContent = String(dashboard.trips ?? "-");
    byId("livePersistence").textContent = String(health.persistenceMode ?? "-").toUpperCase();
    renderFeaturedTrips(trips);

    if (status) {
      status.textContent = "Dashboard live charge.";
      status.classList.remove("danger");
      status.classList.add("success");
    }
  } catch (_) {
    if (status) {
      status.textContent = "Mode public: connectez-vous en admin pour les metriques detaillees.";
      status.classList.remove("success");
      status.classList.add("pending");
    }

    try {
      const trips = await API.request("/trajets");
      renderFeaturedTrips(trips);
    } catch (tripError) {
      renderFeaturedTrips([]);
      if (status) {
        status.textContent = "Backend indisponible pour le moment.";
        status.classList.remove("pending");
        status.classList.add("danger");
      }
    }
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const refreshBtn = byId("refreshLiveMetrics");
  if (refreshBtn) {
    refreshBtn.addEventListener("click", refreshLandingData);
  }
  refreshLandingData();
});
