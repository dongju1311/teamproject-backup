"use client";

import Header from "@/components/commons/Header";
import Footer from "@/components/commons/Footer";
import ScrollToTop from "@/components/commons/ScrollToTop";
import AuthHydrator from "@/app/providers/AuthHydrator.js";
import { useAuthStore } from "@/store/authStore.js";
export default function AppShell({ children }) {
    const authChecked = useAuthStore((s) => s.authChecked);
    return (
        <>
            <AuthHydrator />

            {!authChecked ? (
                <div className="app-loading">
                    {/* 로딩 UI 넣기 */}
                    <p style={{textAlign:"center", paddingTop: "50px"}}>🚀  Loading...</p>
                </div>
            ) : (
                <>
                    <ScrollToTop />
                    <Header />
                    <main>{children}</main>
                    <Footer />
                </>
            )}
        </>
    );
}

