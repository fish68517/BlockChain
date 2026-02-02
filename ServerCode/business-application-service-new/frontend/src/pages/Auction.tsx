import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, Spin, Result, Row, Col, Typography } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { BidForm, AuctionStatus } from '@/components/auction';
import { listingService } from '@/services';
import type { ProjectListing } from '@/types';

const { Title } = Typography;

export const Auction: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [listing, setListing] = useState<ProjectListing | null>(null);
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
    } catch (err) {
      setError('Failed to load auction');
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
        title="Failed to load auction"
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

      <Title level={2}>Auction: {listing.title}</Title>

      <Row gutter={[24, 24]}>
        <Col xs={24} lg={12}>
          <Card title="Place Your Bid">
            <BidForm
              listingId={listing.id}
              currentBid={listing.valueEstimation || 0}
              onSuccess={() => fetchData(listing.id)}
            />
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <AuctionStatus listing={listing} />
        </Col>
      </Row>
    </div>
  );
};
