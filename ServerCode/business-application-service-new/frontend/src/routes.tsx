import { createBrowserRouter } from 'react-router-dom';
import { CreateListing, AdminPanel, RestorerPanel, Dashboard, Listings, ListingDetailPage, Investment, Auction } from '@/pages';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Dashboard />,
  },
  {
    path: '/listings',
    element: <Listings />,
  },
  {
    path: '/listings/:id',
    element: <ListingDetailPage />,
  },
  {
    path: '/create',
    element: <CreateListing />,
  },
  {
    path: '/admin',
    element: <AdminPanel />,
  },
  {
    path: '/restorer',
    element: <RestorerPanel />,
  },
  {
    path: '/invest/:id',
    element: <Investment />,
  },
  {
    path: '/auction/:id',
    element: <Auction />,
  },
]);

export default router;
