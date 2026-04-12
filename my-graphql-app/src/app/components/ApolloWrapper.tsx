// components/ApolloWrapper.tsx
'use client';                          // 👈 only this file is client

import { ApolloProvider } from '@apollo/client/react';
import client from '../lib/apolloClient';

type Props = {
    readonly children: React.ReactNode;
};

export default function ApolloWrapper({ children }: Props) {
    return (
        <ApolloProvider client={client}>
            {children}
        </ApolloProvider>
    );
}