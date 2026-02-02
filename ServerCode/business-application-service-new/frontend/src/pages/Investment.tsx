import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, Spin, Result, Row, Col, Typography } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { InvestForm, InvestorList } from '@/components/investment';
import { listingService, investmentService } from '@/services';
import type { ProjectListing, Investment as InvestmentType } from '@/types';

const { Title, Text } = Typography;

export const Investment: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [listing, setListing] = useState<ProjectListing | null>(null);
  const [investments, setInvestments] = useState<InvestmentType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      fetchData(Number(id));
    }
  }, [id]);

  const fetchData = async (listingId: number) => {
    try {
      setLoading(true);
      const data = await listingService.getById(listingId);
      setListing(data as unknown as ProjectListing);

      // Fetch investments for this listing
      const investmentsRes = await investmentService.getByListing(listingId);
      setInvestments(investmentsRes.data || []);
    } catch (err) {
      setError('Failed to load project');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <Spin size="large" />
      </div>
    );
  }

  if (error || !listing) {
    return (
      <Result
        status="error"
        title="Failed to load project"
        subTitle={error}
        extra={
          <Button type="primary" onClick={() => navigate('/listings')}>
            Back to Listings
          </Button>
        }
      />
    );
  }

  return (
    <div className="space-y-4">
      <Button
        icon={<ArrowLeftOutlined />}
        onClick={() => navigate(`/listings/${id}`)}
      >
        Back to Project
      </Button>

      <Title level={2}>Invest in {listing.title}</Title>

      <Row gutter={[24, 24]}>
        <Col xs={24} lg={12}>
          <Card title="Make Investment">
            <div className="mb-4">
              <Text type="secondary">
                Value Estimation: {listing.valueEstimation || 'N/A'} ETH
              </Text>
            </div>
            <InvestForm
              listingId={listing.id}
              onSuccess={() => fetchData(listing.id)}
            />
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card title="Investors">
            <InvestorList investments={investments} />
          </Card>
        </Col>
      </Row>
    </div>
  );
};
