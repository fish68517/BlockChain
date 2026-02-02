import { Layout, Menu, Card } from 'antd';
import { Link, useLocation } from 'react-router-dom';
import {
  DashboardOutlined,
  UnorderedListOutlined,
  PlusCircleOutlined,
  TeamOutlined,
  WalletOutlined,
  ToolOutlined,
  DollarOutlined,
  ShoppingCartOutlined,
  CrownOutlined,
} from '@ant-design/icons';
import { useMemo } from 'react';

const { Sider } = Layout;

type UserRole = 'ADMIN' | 'OWNER' | 'RESTORER' | 'INVESTOR' | 'BUYER' | 'USER';

const getMenuItemsByRole = (role: UserRole | null) => {
  const commonItems = [
    {
      key: '/',
      icon: <DashboardOutlined />,
      label: <Link to="/">Dashboard</Link>,
    },
    {
      key: '/listings',
      icon: <UnorderedListOutlined />,
      label: <Link to="/listings">Listings</Link>,
    },
  ];

  const roleSpecificItems: Record<UserRole, any[]> = {
    ADMIN: [
      ...commonItems,
      {
        key: '/create',
        icon: <PlusCircleOutlined />,
        label: <Link to="/create">Create Listing</Link>,
      },
      {
        key: '/admin',
        icon: <TeamOutlined />,
        label: <Link to="/admin">Admin Panel</Link>,
      },
      {
        key: '/restorer',
        icon: <ToolOutlined />,
        label: <Link to="/restorer">Restorer Panel</Link>,
      },
    ],
    OWNER: [
      ...commonItems,
      {
        key: '/create',
        icon: <PlusCircleOutlined />,
        label: <Link to="/create">Create Listing</Link>,
      },
      {
        key: '/my-items',
        icon: <CrownOutlined />,
        label: <Link to="/my-items">My Items</Link>,
      },
    ],
    RESTORER: [
      ...commonItems,
      {
        key: '/restorer',
        icon: <ToolOutlined />,
        label: <Link to="/restorer">Restoration Tasks</Link>,
      },
    ],
    INVESTOR: [
      ...commonItems,
      {
        key: '/investments',
        icon: <DollarOutlined />,
        label: <Link to="/investments">My Investments</Link>,
      },
    ],
    BUYER: [
      ...commonItems,
      {
        key: '/my-purchases',
        icon: <ShoppingCartOutlined />,
        label: <Link to="/my-purchases">My Purchases</Link>,
      },
    ],
    USER: commonItems,
  };

  return roleSpecificItems[role || 'USER'] || commonItems;
};

interface SidebarProps {
  collapsed?: boolean;
  onCollapse?: (collapsed: boolean) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ collapsed = false, onCollapse }) => {
  const location = useLocation();
  const role = localStorage.getItem('role') as UserRole | null;
  const walletAddress = localStorage.getItem('walletAddress');
  const ethBalance = localStorage.getItem('ethBalance');
  
  const menuItems = useMemo(() => getMenuItemsByRole(role), [role]);

  return (
    <Sider
      collapsible
      collapsed={collapsed}
      onCollapse={onCollapse}
      width={240}
      style={{
        background: 'linear-gradient(180deg, #1e3a5f 0%, #0f2744 100%)',
        borderRight: 'none',
        boxShadow: '2px 0 8px rgba(0,0,0,0.1)',
      }}
    >
      {/* Logo */}
      <div style={{ 
        padding: '20px 16px', 
        borderBottom: '1px solid rgba(255,255,255,0.1)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: collapsed ? 'center' : 'flex-start',
      }}>
        <span style={{ fontSize: 28 }}>💎</span>
        {!collapsed && (
          <span style={{ 
            marginLeft: 12,
            fontSize: 18,
            fontWeight: 'bold',
            color: '#ffffff',
          }}>
            CollectorCoin
          </span>
        )}
      </div>

      {/* Navigation Menu */}
      <Menu
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        style={{
          background: 'transparent',
          borderRight: 0,
          marginTop: 8,
        }}
        theme="dark"
      />

      {/* Wallet Info Card */}
      {!collapsed && walletAddress && (
        <div style={{ padding: '16px', position: 'absolute', bottom: 60, left: 0, right: 0 }}>
          <Card
            size="small"
            style={{
              background: 'rgba(255,255,255,0.1)',
              border: '1px solid rgba(255,255,255,0.2)',
              borderRadius: 12,
              backdropFilter: 'blur(10px)',
            }}
          >
            <div style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12, marginBottom: 8 }}>
              <WalletOutlined style={{ color: '#60a5fa', marginRight: 6 }} />
              {role || 'User'} Wallet
            </div>
            <div style={{ color: '#ffffff', fontSize: 14, fontWeight: 600 }}>
              {ethBalance || '0'} ETH
            </div>
            <div style={{ color: 'rgba(255,255,255,0.6)', fontSize: 11, marginTop: 4 }}>
              {walletAddress.slice(0, 6)}...{walletAddress.slice(-4)}
            </div>
          </Card>
        </div>
      )}
    </Sider>
  );
};
