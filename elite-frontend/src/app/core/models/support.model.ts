export type SupportCategory = 'CONCERN' | 'ISSUE' | 'MISALIGNMENT' | 'OTHER';
export type SupportTicketStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';

export interface SupportMessage {
  id?: string;
  ticketId?: string;
  authorId?: string;
  authorRole?: string;
  body: string;
  createdAt?: string;
}

export interface SupportTicket {
  id?: string;
  studentId?: string;
  studentName?: string;
  subject: string;
  body: string;
  category: SupportCategory;
  status?: SupportTicketStatus;
  resolutionNotes?: string;
  resolvedBy?: string;
  createdAt?: string;
  updatedAt?: string;
  messages?: SupportMessage[];
}

export interface UpdateSupportStatusRequest {
  status: SupportTicketStatus;
  resolutionNotes?: string;
}
