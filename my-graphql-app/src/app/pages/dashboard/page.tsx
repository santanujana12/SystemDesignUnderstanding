import UserList from "./userList/userList";

export default function Dashboard() {
    return (
        <div className="flex-1 p-4">
            <h1 className="text-2xl font-bold mb-4">Dashboard</h1>
            <p>Welcome to your dashboard! Here you can manage your settings and view your activity.</p>

            <h2 className="text-xl font-semibold mt-6 mb-2">User List</h2>
            {/* UserList component will go here */}
            <UserList />
        </div>
    );
}