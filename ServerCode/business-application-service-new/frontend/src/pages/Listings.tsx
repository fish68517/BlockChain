import { useState, useEffect } from 'react';
import { Row, Col, Input, Select, Pagination, Spin, Empty, Typography } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { ListingCard } from '@/components/listing/ListingCard';
import { listingService } from '@/services';
import type { ProjectListing, ListingStatus } from '@/types';

const { Title } = Typography;
const { Option } = Select;

const PAGE_SIZE = 12;

export const Listings: React.FC = () => {
  const [listings, setListings] = useState<ProjectListing[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState<ListingStatus | 'ALL'>('ALL');
  const [currentPage, setCurrentPage] = useState(1);

  useEffect(() => {
    fetchListings();
  }, []);

  const fetchListings = async () => {
    try {
      setLoading(true);
      const data = await listingService.getAll();
      setListings(data as unknown as ProjectListing[]);
    } catch (error) {
      console.error('Failed to fetch listings:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredListings = listings.filter((listing) => {
    const matchesSearch = listing.title
      .toLowerCase()
      .includes(searchText.toLowerCase());
    const matchesStatus =
      statusFilter === 'ALL' || listing.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const paginatedListings = filteredListings.slice(
    (currentPage - 1) * PAGE_SIZE,
    currentPage * PAGE_SIZE
  );

  return (
    <div className="space-y-6">
      <Title level={2}>Project Listings</Title>

      <div className="flex flex-wrap gap-4">
        <Input
          placeholder="Search projects..."
          prefix={<SearchOutlined />}
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value);
            setCurrentPage(1);
          }}
          className="w-64"
        />
        <Select
          value={statusFilter}
          onChange={(value) => {
            setStatusFilter(value);
            setCurrentPage(1);
          }}
          className="w-40"
        >
          <Option value="ALL">All Status</Option>
          <Option value="PENDING">Pending</Option>
          <Option value="VERIFIED">Verified</Option>
          <Option value="ESTIMATED">Estimated</Option>
          <Option value="LAUNCHED">Launched</Option>
          <Option value="FUNDING">Funding</Option>
          <Option value="FUNDED">Funded</Option>
          <Option value="RESTORING">Restoring</Option>
          <Option value="RESTORED">Restored</Option>
          <Option value="AUCTION">Auction</Option>
          <Option value="SOLD">Sold</Option>
        </Select>
      </div>

      {loading ? (
        <div className="flex justify-center py-12">
          <Spin size="large" />
        </div>
      ) : paginatedListings.length === 0 ? (
        <Empty description="No projects found" />
      ) : (
        <>
          <Row gutter={[16, 16]}>
            {paginatedListings.map((listing) => (
              <Col key={listing.id} xs={24} sm={12} md={8} lg={6}>
                <ListingCard listing={listing} onDelete={fetchListings} />
              </Col>
            ))}
          </Row>

          {filteredListings.length > PAGE_SIZE && (
            <div className="flex justify-center mt-6">
              <Pagination
                current={currentPage}
                total={filteredListings.length}
                pageSize={PAGE_SIZE}
                onChange={setCurrentPage}
                showSizeChanger={false}
              />
            </div>
          )}
        </>
      )}
    </div>
  );
};
