import { useState, useEffect } from 'react';
import { Card, Table, Tag, Button, Space, Typography, Statistic, Row, Col, Empty, Progress } from 'antd';
import { 
  DollarOutlined, 
  EyeOutlined, 
  RiseOutlined,
  FundOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { investmentApi } from '@/services/api';

const { Title, Text } = Typography;

interface Investment {
  id: number;
  listingId: number;
  userId: number;
  amount: number;
  investorAddress: string;
  transactionHash?: string;
  status: string;
  createdAt: string;
  returnAmount?: number;
  percentage?: number;
}

export const MyInvestments: React.FC = () => {
  const [investments, setInvestments] = useState<Investment[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    if (userId) {
      fetchMyInvestments();
    }
  }, [userId]);

  const fetchMyInvestments = async () => {
    try {
      const response: any = await investmentApi.getInvestmentsByUser(Number(userId));
      setInvestments(response || []);
    } catch (error) {
      console.error('Failed to fetch investments:', error);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: 'Item',
      dataIndex: 'listingTitle',
      key: 'listingTitle',
      render: (text: string) => (
        <Space>
          <FundOutlined style={{ color: '#3b82f6' }} />
          <Text strong>{text}</Text>
        </Space>
      ),
    },
    {
      title: 'Amount Invested',
      dataIndex: 'amount',
      key: 'amount',
      render: (amount: number) => <Text strong>{amount} ETH</Text>,
    },
    {
      title: 'Share',
      dataIndex: 'percentage',
      key: 'percentage',
      render: (percentage: number) => (
        <Progress percent={percentage} size="small" style={{ width: 100 }} />
      ),
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const colors: Record<string, string> = {
          ACTIVE: 'blue',
          COMPLETED: 'green',
          PENDING: 'orange',
        };
        return <Tag color={colors[status] || 'default'}>{status}</Tag>;
      },
    },
    {
      title: 'Return',
      dataIndex: 'returnAmount',
      key: 'returnAmount',
      render: (returnAmount: number, record: Investment) => {
        if (!returnAmount) return '-';
        const profit = returnAmount - record.amount;
        const profitPercent = ((profit / record.amount) * 100).toFixed(1);
        return (
          <Space>
            <Text strong style={{ color: profit > 0 ? '#10b981' : '#ef4444' }}>
              {returnAmount} ETH
            </Text>
            <Tag color={profit > 0 ? 'green' : 'red'}>
              {profit > 0 ? '+' : ''}{profitPercent}%
            </Tag>
          </Space>
        );
      },
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: any, record: Investment) => (
        <Button 
          icon={<EyeOutlined />} 
          onClick={() => navigate(`/listings/${record.listingId}`)}
        >
          View
        </Button>
      ),
    },
  ];

  const totalInvested = investments.reduce((sum, inv) => sum + (inv.amount || 0), 0);
  const totalReturns = investments.reduce((sum, inv) => sum + (inv.returnAmount || 0), 0);
  const activeCount = investments.filter(inv => inv.status === 'ACTIVE').length;

  return (
    <div style={{ padding: '24px' }}>
      <Title level={2}>
        <DollarOutlined style={{ marginRight: 12, color: '#10b981' }} />
        My Investments
      </Title>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="Total Investments"
              value={investments.length}
              prefix={<FundOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Active Investments"
              value={activeCount}
              valueStyle={{ color: '#3b82f6' }}
              prefix={<RiseOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Total Invested"
              value={totalInvested}
              precision={2}
              suffix="ETH"
              valueStyle={{ color: '#f59e0b' }}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Total Returns"
              value={totalReturns}
              precision={2}
              suffix="ETH"
              valueStyle={{ color: '#10b981' }}
              prefix={<DollarOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card>
        {investments.length > 0 ? (
          <Table
            columns={columns}
            dataSource={investments}
            rowKey="id"
            loading={loading}
            pagination={{ pageSize: 10 }}
          />
        ) : (
          <Empty
            description="You haven't made any investments yet"
            image={Empty.PRESENTED_IMAGE_SIMPLE}
          >
            <Button type="primary" onClick={() => navigate('/listings')}>
              Browse Listings
            </Button>
          </Empty>
        )}
      </Card>
    </div>
  );
};
