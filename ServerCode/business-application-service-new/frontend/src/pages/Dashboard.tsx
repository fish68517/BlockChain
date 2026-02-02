import { useEffect, useMemo } from 'react';
import { Card, Row, Col, Statistic, Button, Space, Tag, Spin, Table } from 'antd';
import {
  ProjectOutlined,
  DollarOutlined,
  PlusOutlined,
  RiseOutlined,
  UnorderedListOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import { fetchListings } from '@/store/slices/listingSlice';
import { ListingStatus } from '@/types';

// Status tag color mapping
const statusColorMap: Record<string, string> = {
  [ListingStatus.PENDING]: 'default',
  [ListingStatus.VERIFIED]: 'processing',
  [ListingStatus.ESTIMATED]: 'cyan',
  [ListingStatus.LAUNCHED]: 'purple',
  [ListingStatus.FUNDING]: 'warning',
  [ListingStatus.FUNDED]: 'success',
  [ListingStatus.RESTORING]: 'gold',
  [ListingStatus.RESTORED]: 'lime',
  [ListingStatus.AUCTION]: 'magenta',
  [ListingStatus.SOLD]: 'success',
  [ListingStatus.REJECTED]: 'error',
};

export const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { items: listings, loading } = useAppSelector((state) => state.listing);

  useEffect(() => {
    dispatch(fetchListings());
  }, [dispatch]);

  // Calculate statistics
  const stats = useMemo(() => {
    const totalProjects = listings.length;
    const totalInvestment = listings.reduce(
      (sum, item) => sum + (item.valueEstimation || 0),
      0
    );
    const fundingProjects = listings.filter(
      (item) => item.status === ListingStatus.FUNDING
    ).length;
    const completedAuctions = listings.filter(
      (item) => item.status === ListingStatus.SOLD
    ).length;

    return { totalProjects, totalInvestment, fundingProjects, completedAuctions };
  }, [listings]);

  // Get recent 5 projects
  const recentProjects = useMemo(() => {
    return [...listings]
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 5);
  }, [listings]);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Statistics cards - Blue theme */}
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            hoverable 
            className="hover-card"
            style={{ 
              background: '#ffffff', 
              border: '1px solid #e2e8f0',
              borderRadius: 12,
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b' }}>Total Projects</span>}
              value={stats.totalProjects}
              prefix={<ProjectOutlined style={{ color: '#3b82f6' }} />}
              styles={{ content: { color: '#1e293b', fontWeight: 'bold', fontSize: 28 } }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            hoverable 
            className="hover-card"
            style={{ 
              background: '#ffffff', 
              border: '1px solid #e2e8f0',
              borderRadius: 12,
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b' }}>Total Value</span>}
              value={stats.totalInvestment}
              prefix={<DollarOutlined style={{ color: '#10b981' }} />}
              suffix={<span style={{ color: '#64748b', fontSize: 14 }}>ETH</span>}
              precision={2}
              styles={{ content: { color: '#1e293b', fontWeight: 'bold', fontSize: 28 } }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            hoverable 
            className="hover-card"
            style={{ 
              background: '#ffffff', 
              border: '1px solid #e2e8f0',
              borderRadius: 12,
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b' }}>Funding Projects</span>}
              value={stats.fundingProjects}
              prefix={<ProjectOutlined style={{ color: '#f59e0b' }} />}
              styles={{ content: { color: '#1e293b', fontWeight: 'bold', fontSize: 28 } }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            hoverable 
            className="hover-card"
            style={{ 
              background: '#ffffff', 
              border: '1px solid #e2e8f0',
              borderRadius: 12,
            }}
          >
            <Statistic
              title={<span style={{ color: '#64748b' }}>Completed Auctions</span>}
              value={stats.completedAuctions}
              prefix={<RiseOutlined style={{ color: '#8b5cf6' }} />}
              styles={{ content: { color: '#1e293b', fontWeight: 'bold', fontSize: 28 } }}
            />
          </Card>
        </Col>
      </Row>

      {/* Quick actions */}
      <Card 
        title={<span style={{ color: '#1e293b', fontWeight: 600 }}>⚡ Quick Actions</span>}
        style={{ 
          background: '#ffffff', 
          border: '1px solid #e2e8f0',
          borderRadius: 12,
        }}
        styles={{ header: { borderBottom: '1px solid #e2e8f0' } }}
      >
        <Space wrap size="middle">
          <Button
            type="primary"
            size="large"
            icon={<PlusOutlined />}
            onClick={() => navigate('/create')}
            style={{ 
              background: 'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)', 
              border: 'none',
              fontWeight: 500,
              borderRadius: 8,
            }}
          >
            Create Project
          </Button>
          <Button
            size="large"
            icon={<UnorderedListOutlined />}
            onClick={() => navigate('/listings')}
            style={{ 
              background: '#ffffff', 
              border: '1px solid #3b82f6',
              color: '#3b82f6',
              borderRadius: 8,
            }}
          >
            View All Listings
          </Button>
          <Button
            size="large"
            icon={<SettingOutlined />}
            onClick={() => navigate('/admin')}
            style={{ 
              background: '#ffffff', 
              border: '1px solid #e2e8f0',
              color: '#64748b',
              borderRadius: 8,
            }}
          >
            Admin Panel
          </Button>
        </Space>
      </Card>

      {/* Recent projects table */}
      <Card
        title={<span style={{ color: '#1e293b', fontWeight: 600 }}>📋 Recent Projects</span>}
        extra={<Link to="/listings" style={{ color: '#3b82f6' }}>View All →</Link>}
        style={{ 
          background: '#ffffff', 
          border: '1px solid #e2e8f0',
          borderRadius: 12,
        }}
        styles={{ header: { borderBottom: '1px solid #e2e8f0' } }}
      >
        <Table
          dataSource={recentProjects}
          rowKey="id"
          pagination={false}
          locale={{ emptyText: <span style={{ color: '#64748b' }}>No projects yet</span> }}
          columns={[
            {
              title: <span style={{ color: '#64748b' }}>Project</span>,
              dataIndex: 'title',
              key: 'title',
              render: (text: string, record: { id: number }) => (
                <Link to={`/listings/${record.id}`} style={{ color: '#3b82f6', fontWeight: 500 }}>
                  {text}
                </Link>
              ),
            },
            {
              title: <span style={{ color: '#64748b' }}>Description</span>,
              dataIndex: 'description',
              key: 'description',
              render: (text: string) => (
                <span style={{ color: '#1e293b' }}>{text || 'No description'}</span>
              ),
            },
            {
              title: <span style={{ color: '#64748b' }}>Status</span>,
              dataIndex: 'status',
              key: 'status',
              render: (status: string) => (
                <Tag color={statusColorMap[status]}>{status}</Tag>
              ),
            },
            {
              title: <span style={{ color: '#64748b' }}>Action</span>,
              key: 'action',
              render: (_: unknown, record: { id: number }) => (
                <Button 
                  type="link" 
                  onClick={() => navigate(`/listings/${record.id}`)}
                  style={{ color: '#3b82f6' }}
                >
                  View Details
                </Button>
              ),
            },
          ]}
        />
      </Card>
    </div>
  );
};
