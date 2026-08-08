package com.eliteschool.wallet_service.dto;

import com.eliteschool.wallet_service.model.Wallet;
import java.util.List;
import java.util.stream.Collectors;

public class WalletMapper {

    public static WalletDto toDto(Wallet wallet) {
        if (wallet == null) {
            return null;
        }
        
        return WalletDto.builder()
                .studentId(wallet.getStudentId())
                .balance(wallet.getBalance())
                .build();
    }
    
    public static Wallet toEntity(WalletDto dto) {
        if (dto == null) {
            return null;
        }
        
        return Wallet.builder()
                .studentId(dto.getStudentId())
                .balance(dto.getBalance())
                .build();
    }
    
    public static List<WalletDto> toDtoList(List<Wallet> wallets) {
        if (wallets == null) {
            return List.of();
        }
        
        return wallets.stream()
                .map(WalletMapper::toDto)
                .collect(Collectors.toList());
    }
}
