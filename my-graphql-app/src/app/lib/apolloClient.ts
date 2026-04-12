import { ApolloClient, InMemoryCache, HttpLink } from '@apollo/client';

const client = new ApolloClient({
    // Use 'link' and wrap your URI in an HttpLink
    link: new HttpLink({
        uri: '/api/graphql',
    }),
    cache: new InMemoryCache(),
});

export default client;
