import Link from "next/link";

export default function Home() {
  return (
    <div className="flex-1 p-4">
      <h1 className="text-2xl font-bold mb-4">Welcome to Next.js!</h1>
      <p className="mb-4">Get started by editing <code>src/app/page.tsx</code></p>
      <Link href="/pages/dashboard">Go to dashboard</Link>
    </div>
  );
}
