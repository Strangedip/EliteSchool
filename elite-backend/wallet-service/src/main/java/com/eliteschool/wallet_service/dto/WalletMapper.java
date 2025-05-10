package com.eliteschool.wallet_service.dto;

import com.eliteschool.wallet_service.model.Wallet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Wallet and WalletDto
 */
public class WalletMapper {

    /**
     * Convert a Wallet entity to a WalletDto
     * @param wallet The wallet entity
     * @return The wallet DTO
     */
    public static WalletDto toDto(Wallet wallet) {
        if (wallet == null) {
            return null;
        }
        
        return WalletDto.builder()
                .studentId(wallet.getStudentId())
                .balance(wallet.getBalance())
                .build();
    }
    
    /**
     * Convert a WalletDto to a Wallet entity
     * @param dto The wallet DTO
     * @return The wallet entity
     */
    public static Wallet toEntity(WalletDto dto) {
        if (dto == null) {
            return null;
        }
        
        return Wallet.builder()
                .studentId(dto.getStudentId())
                .balance(dto.getBalance())
                .build();
    }
    
    /**
     * Convert a list of Wallet entities to a list of WalletDtos
     * @param wallets The list of wallet entities
     * @return The list of wallet DTOs
     */
    public static List<WalletDto> toDtoList(List<Wallet> wallets) {
        if (wallets == null) {
            return List.of();
        }
        
        return wallets.stream()
                .map(WalletMapper::toDto)
                .collect(Collectors.toList());
    }
} 