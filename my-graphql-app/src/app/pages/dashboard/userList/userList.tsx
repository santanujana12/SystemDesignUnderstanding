// components/UserList.jsx
'use client';
import { User } from '@/app/lib/resolvers';
import { gql } from '@apollo/client';
import { useQuery } from '@apollo/client/react';


const GET_USERS = gql`
  query {
    users {
      id
      name
      email
    }
  }
`;

export default function UserList() {
    const { loading, error, data } = useQuery<{ users: User[] }>(GET_USERS);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Something went wrong!</p>;

    return (
        <ul>
            {data?.users.map(u => (
                <li key={u.id}>{u.name} — {u.email}</li>
            ))}
        </ul>
    );
}