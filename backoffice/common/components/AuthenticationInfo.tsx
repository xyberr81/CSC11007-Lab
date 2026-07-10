import apiClientService from '@commonServices/ApiClientService';
import Link from 'next/link';
import { useEffect, useState } from 'react';

const baseUrl = '/authentication/user';

export default function AuthenticationInfo() {
  type AuthenticatedUser = {
    username: string;
  };

  const [authenticatedUser, setAuthenticatedUser] = useState<AuthenticatedUser | null>(null);

  async function getAuthenticatedUser() {
    const response = await apiClientService.get(baseUrl);
    if (response.status === 401) {
      window.location.assign('/oauth2/authorization/api-client');
      return null;
    }
    return response.json();
  }

  useEffect(() => {
    getAuthenticatedUser().then((data) => {
      if (data) {
        setAuthenticatedUser(data);
      }
    });
  }, []);

  if (!authenticatedUser) {
    return null;
  }

  return (
    <>
      Signed in as: <Link href="/profile">{authenticatedUser.username}</Link>{' '}
      <Link href="/logout">Logout</Link>
    </>
  );
}
