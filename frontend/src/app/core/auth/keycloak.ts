import { Injectable } from "@angular/core";
import Keycloak, { KeycloakInstance } from "keycloak-js";

@Injectable({
  providedIn: "root",
})
export class KeycloakService {
  private keycloak: KeycloakInstance;

  constructor() {
    this.keycloak = new Keycloak({
      url: 'https://d28ry4mculifvr.cloudfront.net',
      realm: "smarttask",
      clientId: "smarttask-frontend",
    });
  }

  async init(): Promise<boolean> {
    return this.keycloak.init({
      onLoad: "login-required",
      pkceMethod: "S256",
      checkLoginIframe: false
    });
  }

  async login(): Promise<void> {
    await this.keycloak.login({
      redirectUri: window.location.origin,
    });
  }

  async logout(): Promise<void> {
    await this.keycloak.logout({
      redirectUri: window.location.origin,
    });
  }

  async updateToken(): Promise<boolean> {
    try {
      return await this.keycloak.updateToken(30);
    } catch (error) {
      console.error("Failed to refresh token", error);
      return false;
    }
  }

  getToken(): string | undefined {
    return this.keycloak.token;
  }

  isAuthenticated(): boolean {
    return this.keycloak.authenticated ?? false;
  }

  getUsername(): string | undefined {
    return this.keycloak.tokenParsed?.["preferred_username"];
  }

  getRoles(): string[] {
    return this.keycloak.realmAccess?.roles ?? [];
  }
}
