import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Button, Spin, Result, Space } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { ListingDetail } from '@/components/listing/ListingDetail';
import { listingService } from '@/services';
import type { ProjectListing } from '@/types';

export const ListingDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [listing, setListing] = useState<ProjectListing | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      fetchListing(Number(id));
    }
  }, [id]);

  const fetchListing = async (listingId: number) => {
    try {
      setLoading(true);
      setError(null);
      const data = await listingService.getById(listingId);
      setListing(data as unknown as ProjectListing);
    } catch (err) {
      setError('Failed to load project details');
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
        onClick={() => navigate('/listings')}
      >
        Back to Listings
      </Button>

      <ListingDetail listing={listing} />

      <ActionButtons listing={listing} />
    </div>
  );
};

const ActionButtons: React.FC<{ listing: ProjectListing }> = ({ listing }) => {
  const navigate = useNavigate();
  const { status } = listing;

  return (
    <Space className="mt-4">
      {(status === 'LAUNCHED' || status === 'FUNDING') && (
        <Button type="primary" size="large" onClick={() => navigate(`/invest/${listing.id}`)}>
          💰 Invest Now
        </Button>
      )}
      {status === 'AUCTION' && (
        <Button type="primary" size="large" onClick={() => navigate(`/auction/${listing.id}`)}>
          🔨 Place Bid
        </Button>
      )}
    </Space>
  );
};
