// lib/resolvers.js

export type User = {
    id: string;
    name: string;
    email: string;
}

let users: User[] = [
    { id: '1', name: 'Alice', email: 'alice@gmail.com' },
    { id: '2', name: 'Bob', email: 'bob@gmail.com' },
];

export const resolvers = {
    Query: {
        users: (): User[] => users,
        user: (_: unknown, { id }: { id: string }): User | undefined =>
            users.find(u => u.id === id),
    },
    Mutation: {
        createUser: (_: unknown, { name, email }: { name: string; email: string }): User => {
            const newUser = { id: String(Date.now()), name, email };
            users.push(newUser);
            return newUser;
        },
        deleteUser: (_: unknown, { id }: { id: string }): string => {
            users = users.filter(u => u.id !== id);
            return `User ${id} deleted`;
        }
    }
};