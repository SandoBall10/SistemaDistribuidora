interface JwtPayload {
  rol?: string;
}

function base64UrlDecode(input: string): string {
  const base64 = input.replace(/-/g, '+').replace(/_/g, '/');
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
  return atob(padded);
}

export function extractRoleFromAccessToken(accessToken: string): string | null {
  try {
    const tokenParts = accessToken.split('.');
    if (tokenParts.length < 2) {
      return null;
    }

    const payloadRaw = base64UrlDecode(tokenParts[1]);
    const payload = JSON.parse(payloadRaw) as JwtPayload;
    return payload.rol ?? null;
  } catch {
    return null;
  }
}
