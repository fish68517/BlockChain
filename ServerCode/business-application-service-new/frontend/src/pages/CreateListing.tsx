import { useState } from 'react';
import { Card, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';
import { ListingForm } from '@/components/listing/ListingForm';
import { listingService } from '@/services';
import type { CreateListingRequest } from '@/types';

const { Title } = Typography;

export const CreateListing: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (values: CreateListingRequest) => {
    setLoading(true);
    try {
      await listingService.create(values);
      navigate('/listings');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <Title level={2}>Create New Project</Title>
      <Card className="mt-4">
        <ListingForm onSubmit={handleSubmit} loading={loading} />
      </Card>
    </div>
  );
};
