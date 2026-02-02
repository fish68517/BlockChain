import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { ConfigProvider, App as AntdApp } from 'antd'
import { MainLayout, ProtectedRoute } from '@/components/common'
import {
  Dashboard,
  Listings,
  ListingDetailPage,
  CreateListing,
  AdminPanel,
  RestorerPanel,
  Investment,
  Auction,
  MyItems,
  MyInvestments,
  MyPurchases,
} from '@/pages'
import { Register } from '@/pages/Register'
import { Login } from '@/pages/Login'

function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#3b82f6',
          colorBgContainer: '#ffffff',
          colorBgElevated: '#ffffff',
          colorBorder: '#e2e8f0',
          colorText: '#1e293b',
          colorTextSecondary: '#64748b',
          borderRadius: 12,
        },
        components: {
          Card: {
            colorBgContainer: '#ffffff',
            headerFontSize: 16,
          },
          Button: {
            primaryShadow: '0 4px 14px rgba(59, 130, 246, 0.3)',
          },
          Table: {
            colorBgContainer: '#ffffff',
            headerBg: '#f8fafc',
            rowHoverBg: 'rgba(59, 130, 246, 0.05)',
            borderColor: '#e2e8f0',
          },
          Menu: {
            darkItemBg: 'transparent',
            darkItemSelectedBg: 'rgba(59, 130, 246, 0.2)',
            darkItemHoverBg: 'rgba(59, 130, 246, 0.1)',
            darkItemSelectedColor: '#60a5fa',
          },
          Input: {
            colorBgContainer: '#ffffff',
            colorBorder: '#e2e8f0',
          },
          Select: {
            colorBgContainer: '#ffffff',
            colorBorder: '#e2e8f0',
          },
        },
      }}
    >
      <AntdApp>
        <BrowserRouter>
          <Routes>
            <Route path="/register" element={<Register />} />
            <Route path="/login" element={<Login />} />
            <Route path="/" element={<MainLayout />}>
              <Route index element={<Dashboard />} />
              <Route path="listings" element={<Listings />} />
              <Route path="listings/:id" element={<ListingDetailPage />} />
              <Route path="create" element={
                <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
                  <CreateListing />
                </ProtectedRoute>
              } />
              <Route path="admin" element={
                <ProtectedRoute allowedRoles={['ADMIN']}>
                  <AdminPanel />
                </ProtectedRoute>
              } />
              <Route path="restorer" element={
                <ProtectedRoute allowedRoles={['ADMIN', 'RESTORER']}>
                  <RestorerPanel />
                </ProtectedRoute>
              } />
              <Route path="my-items" element={
                <ProtectedRoute allowedRoles={['ADMIN', 'OWNER']}>
                  <MyItems />
                </ProtectedRoute>
              } />
              <Route path="investments" element={
                <ProtectedRoute allowedRoles={['ADMIN', 'INVESTOR']}>
                  <MyInvestments />
                </ProtectedRoute>
              } />
              <Route path="my-purchases" element={
                <ProtectedRoute allowedRoles={['ADMIN', 'BUYER']}>
                  <MyPurchases />
                </ProtectedRoute>
              } />
              <Route path="invest/:id" element={<Investment />} />
              <Route path="auction/:id" element={<Auction />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AntdApp>
    </ConfigProvider>
  )
}

export default App
